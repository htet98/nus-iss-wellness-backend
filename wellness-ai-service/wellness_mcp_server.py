"""
Wellness MCP Server
Exposes the three wellness tools over stdio so any MCP client can use them.

Run standalone:  python wellness_mcp_server.py
Or via client:   MultiServerMCPClient launches this as a subprocess automatically.

Author: Htet Nandar
"""
import sys
import os

# Allow importing from services/
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from mcp.server.fastmcp import FastMCP
from services.vector_store import build_or_load_collection
from services import wellness_tools as wt

mcp = FastMCP("Wellness Tools")

# Load the existing ChromaDB collection (built by the main service on startup)
_collection = build_or_load_collection()
wt.set_collection(_collection)


@mcp.tool()
def search_wellness_knowledge(query: str) -> str:
    """Search the wellness knowledge base for evidence-based information on
    nutrition, exercise, sleep, mental health, stress management, and
    other wellness topics. Use this whenever the user asks a wellness
    question that requires factual information."""
    return wt.search_wellness_knowledge(query)


@mcp.tool()
def calculate_bmi(weight_kg: float, height_m: float) -> str:
    """Calculate Body Mass Index (BMI) and WHO weight category.
    weight_kg: body weight in kilograms.
    height_m:  height in metres, e.g. 1.70 for 170 cm."""
    return wt.calculate_bmi(weight_kg, height_m)


@mcp.tool()
def calculate_daily_calories(
    weight_kg: float,
    height_cm: float,
    age: int,
    gender: str,
    activity_level: str,
) -> str:
    """Estimate daily calorie needs (TDEE) using the Mifflin-St Jeor equation.
    activity_level: sedentary | light | moderate | active | very_active"""
    return wt.calculate_daily_calories(weight_kg, height_cm, age, gender, activity_level)


if __name__ == "__main__":
    mcp.run(transport="stdio")
