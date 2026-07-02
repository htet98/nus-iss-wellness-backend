"""
Input guardrails for the wellness agent.

Checks run before the agent loop:
  1. Prompt injection — attempts to override system instructions
  2. Off-topic       — questions clearly unrelated to health/wellness
  3. PII warning     — user shares personal identifiable info (not blocked, just noted)

Author: Htet Nandar
"""
import re

# ── Off-topic keyword list ─────────────────────────────────────────────────
_OFF_TOPIC_KEYWORDS = [
    # Finance / crypto
    "bitcoin", "crypto", "cryptocurrency", "stock market", "forex",
    "invest", "trading", "nft",
    # Politics
    "politics", "election", "president", "prime minister", "parliament",
    "democrat", "republican",
    # Malicious
    "hack", "exploit", "malware", "ransomware", "phishing",
    "weapon", "bomb", "explosive",
    # Illegal substances
    "cocaine", "heroin", "methamphetamine",
]

# ── Prompt injection phrases ───────────────────────────────────────────────
_INJECTION_PHRASES = [
    "ignore previous instructions",
    "ignore your instructions",
    "disregard your system prompt",
    "disregard your instructions",
    "forget your previous instructions",
    "you are now",
    "pretend you are",
    "act as if you have no restrictions",
    "act as if you have no guidelines",
    "jailbreak",
    "dan mode",
    "developer mode",
    "override your",
]

# ── PII patterns ───────────────────────────────────────────────────────────
_PII_PATTERNS = [
    re.compile(r'\b\d{3}[-.\s]?\d{3}[-.\s]?\d{4}\b'),                     # phone number
    re.compile(r'\b[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}\b'), # email
    re.compile(r'\b[STFG]\d{7}[A-Z]\b'),                                   # Singapore NRIC
    re.compile(r'\b\d{8,}\b'),                                              # long ID number
]

_PII_WARNING = (
    "⚠️ Your message may contain personal information (e.g. phone number or ID). "
    "Please avoid sharing sensitive personal data in chat."
)

_INJECTION_REFUSAL = (
    "I noticed an attempt to override my instructions. "
    "I'm a wellness assistant — I can only help with health and wellness topics."
)

_OFF_TOPIC_REFUSAL = (
    "I'm a wellness assistant and can only help with health and wellness topics. "
    "Please ask me about nutrition, exercise, sleep, mental health, or other wellness subjects."
)


# ── Public API ─────────────────────────────────────────────────────────────

def check_prompt_injection(message: str) -> bool:
    lower = message.lower()
    return any(phrase in lower for phrase in _INJECTION_PHRASES)


def check_off_topic(message: str) -> bool:
    lower = message.lower()
    return any(kw in lower for kw in _OFF_TOPIC_KEYWORDS)


def check_pii(message: str) -> bool:
    return any(p.search(message) for p in _PII_PATTERNS)


def run_input_guardrails(message: str) -> tuple[str | None, str | None]:
    """
    Run all input guardrails.

    Returns (refusal, warning) where:
      - refusal: a string to return immediately (block the request), or None
      - warning: a string to prepend to the agent's response as a soft warning, or None
    """
    if check_prompt_injection(message):
        return _INJECTION_REFUSAL, None

    if check_off_topic(message):
        return _OFF_TOPIC_REFUSAL, None

    warning = _PII_WARNING if check_pii(message) else None
    return None, warning
