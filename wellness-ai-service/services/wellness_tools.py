"""
Wellness agent tools.

Each tool has:
  - A Python function that executes the action
  - An OpenAI-format schema (used in the tool-calling API)

The LLM decides which tool(s) to call based on the user's message.
Tools follow the @tool pattern from the NUS ISS Single Agent Deep Dive course.

Author: Htet Nandar
"""
import json

# Shared ChromaDB collection reference — set by AgentService after startup
_collection = None


def set_collection(collection) -> None:
    """Inject the ChromaDB collection after the vector store is built."""
    global _collection
    _collection = collection


# ── Tool functions ─────────────────────────────────────────────────────────

def search_wellness_knowledge(query: str, k: int = 5) -> str:
    """
    Search the wellness knowledge base (ChromaDB) for evidence-based
    information on nutrition, exercise, sleep, mental health, and wellness.
    Returns relevant text chunks joined as a single string.
    """
    if _collection is None:
        return "Knowledge base is not available right now."
    results = _collection.query(query_texts=[query], n_results=k)
    docs = results.get("documents", [[]])[0]
    if not docs:
        return "No relevant information found in the knowledge base."
    return "\n\n---\n\n".join(docs)


def calculate_bmi(weight_kg: float, height_m: float) -> str:
    """
    Calculate Body Mass Index (BMI) and return the WHO weight category.
    weight_kg: weight in kilograms
    height_m:  height in metres (e.g. 1.75)
    """
    if height_m <= 0 or weight_kg <= 0:
        return json.dumps({"error": "Weight and height must be positive numbers."})

    bmi = weight_kg / (height_m ** 2)

    if bmi < 18.5:
        category = "Underweight"
        advice = "Consider consulting a dietitian to reach a healthy weight."
    elif bmi < 25.0:
        category = "Normal weight"
        advice = "Great! Maintain your healthy lifestyle."
    elif bmi < 30.0:
        category = "Overweight"
        advice = "Moderate exercise and a balanced diet can help."
    else:
        category = "Obese"
        advice = "Please consult a healthcare provider for personalised guidance."

    return json.dumps({
        "bmi": round(bmi, 1),
        "category": category,
        "advice": advice,
    })


def calculate_daily_calories(
    weight_kg: float,
    height_cm: float,
    age: int,
    gender: str,
    activity_level: str,
) -> str:
    """
    Estimate Total Daily Energy Expenditure (TDEE) using Mifflin-St Jeor equation.

    weight_kg:      weight in kilograms
    height_cm:      height in centimetres
    age:            age in years
    gender:         'male' or 'female'
    activity_level: one of sedentary | light | moderate | active | very_active
    """
    # Mifflin-St Jeor BMR
    if gender.lower() in ("male", "m", "man"):
        bmr = 10 * weight_kg + 6.25 * height_cm - 5 * age + 5
    else:
        bmr = 10 * weight_kg + 6.25 * height_cm - 5 * age - 161

    activity_multipliers = {
        "sedentary":   (1.2,   "Little or no exercise"),
        "light":       (1.375, "Light exercise 1–3 days/week"),
        "moderate":    (1.55,  "Moderate exercise 3–5 days/week"),
        "active":      (1.725, "Hard exercise 6–7 days/week"),
        "very_active": (1.9,   "Very hard exercise or physical job"),
    }

    multiplier, description = activity_multipliers.get(
        activity_level.lower(), (1.2, "Sedentary (default)")
    )
    tdee = bmr * multiplier

    return json.dumps({
        "bmr_kcal":       round(bmr),
        "tdee_kcal":      round(tdee),
        "activity_level": activity_level,
        "description":    description,
        "weight_loss":    round(tdee - 500),   # ~0.5 kg/week deficit
        "weight_gain":    round(tdee + 300),   # lean bulk surplus
    })


# ── OpenAI function-calling tool schemas ───────────────────────────────────

TOOLS = [
    {
        "type": "function",
        "function": {
            "name": "search_wellness_knowledge",
            "description": (
                "Search the wellness knowledge base for evidence-based information "
                "on nutrition, exercise, sleep, mental health, stress management, "
                "and other wellness topics. Use this whenever the user asks a "
                "wellness question that requires factual information."
            ),
            "parameters": {
                "type": "object",
                "properties": {
                    "query": {
                        "type": "string",
                        "description": (
                            "A specific search query, e.g. "
                            "'benefits of meditation for stress' or "
                            "'how much protein per day for muscle gain'"
                        ),
                    }
                },
                "required": ["query"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "calculate_bmi",
            "description": (
                "Calculate the user's Body Mass Index (BMI) and WHO weight category. "
                "Use this when the user provides their weight and height and asks "
                "about their BMI or healthy weight range."
            ),
            "parameters": {
                "type": "object",
                "properties": {
                    "weight_kg": {
                        "type": "number",
                        "description": "Body weight in kilograms",
                    },
                    "height_m": {
                        "type": "number",
                        "description": "Height in metres, e.g. 1.70 for 170 cm",
                    },
                },
                "required": ["weight_kg", "height_m"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "calculate_daily_calories",
            "description": (
                "Estimate the user's daily calorie needs (TDEE) using the "
                "Mifflin-St Jeor equation. Use this when the user asks how many "
                "calories they should eat, or wants a calorie target for weight "
                "loss, maintenance, or muscle gain."
            ),
            "parameters": {
                "type": "object",
                "properties": {
                    "weight_kg": {
                        "type": "number",
                        "description": "Body weight in kilograms",
                    },
                    "height_cm": {
                        "type": "number",
                        "description": "Height in centimetres, e.g. 170",
                    },
                    "age": {
                        "type": "integer",
                        "description": "Age in years",
                    },
                    "gender": {
                        "type": "string",
                        "description": "'male' or 'female'",
                    },
                    "activity_level": {
                        "type": "string",
                        "description": (
                            "Activity level: sedentary | light | moderate | "
                            "active | very_active"
                        ),
                    },
                },
                "required": ["weight_kg", "height_cm", "age", "gender", "activity_level"],
            },
        },
    },
]

# Maps tool name → callable function
TOOL_REGISTRY: dict[str, callable] = {
    "search_wellness_knowledge": search_wellness_knowledge,
    "calculate_bmi":             calculate_bmi,
    "calculate_daily_calories":  calculate_daily_calories,
}
