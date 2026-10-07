---
name: spec-generator
description: Use this skill when the user asks to create, draft, or review a feature specification (spec). Guides a requirements interview and produces a spec.md adhering to the team's template with cross-platform targets (Backend, Web, iOS).
---

# Specification Generator for FOSS Training

Transforms a high-level idea into an agreed specification across the FOSS Training ecosystem (Java Backend, Angular Web, and Swift iOS). The spec is the contract: if something is not written here, it is not implemented.

## Process

1. **Read context.** Check [AGENTS.md](../../AGENTS.md), [foss-training-ios/AGENTS.md](../../foss-training-ios/AGENTS.md), and existing specs in `specs/` to adhere to project conventions and avoid contradicting agreed invariants.
2. **Interview the user.** Ask questions **ONE AT A TIME**, up to a maximum of 6, waiting for the user's response before asking the next. Focus on edge cases, error handling, platform targets (Backend, Web, iOS), and what is out of scope. Do not propose technical solutions: if the user asks "how would you do it?", redirect to the WHAT and WHY.
   Prioritize questions whose answers alter what needs to be built; discard questions that have an obvious default answer.
3. **Select number.** Inspect `specs/` and use the next available 3-digit number: `specs/NNN-<kebab-case-name>/spec.md`.
4. **Draft the spec.** Use `spec-template.md` from this skill without omitting sections. Formulate acceptance criteria **strictly in EARS notation**, numbered as FR-1, FR-2, … Every requirement must be verifiable: if you cannot determine how to test it, it is poorly written.
5. **Flag unknowns.** Mark unknown or undecided details as `[NEEDS CLARIFICATION: specific question]`. Never invent details to fill gaps: a visible gap provides information, whereas a silent assumption is technical debt.
6. **Request explicit approval.** Ask for user approval once drafting is complete. Do not proceed to planning or writing code until approval is granted.

## Rules

- The spec describes **WHAT** and **WHY**. It is forbidden to include tech stack details, architecture, file paths, database schemas, algorithms, or function signatures: those belong in the implementation plan.
- **Always** include the "Out of Scope" section to prevent scope creep.
- One requirement, one sentence. If you need "and" to join two behaviors, split them into two requirements.
- No unmeasurable adjectives: "fast", "intuitive", and "robust" are not valid requirements. State the quantitative threshold or do not include it.
- Language: English (technical documentation standard).

## EARS Notation

Five patterns. Choose the applicable one; do not mix them:

| Pattern | Syntax | When to Use |
|---|---|---|
| Ubiquitous | THE SYSTEM SHALL \<response\> | Always true / continuous behavior |
| Event-driven | WHEN \<trigger\>, THE SYSTEM SHALL \<response\> | Response to an external or internal trigger |
| State-driven | WHILE \<state\>, THE SYSTEM SHALL \<response\> | In effect during a specific system state |
| Optional feature | WHERE \<feature is present\>, THE SYSTEM SHALL \<response\> | Applies only if an optional capability is enabled |
| Unwanted behavior | IF \<unwanted condition\>, THEN THE SYSTEM SHALL \<response\> | Error handling, edge cases, and safety guards |

### Well-Written Example:

> FR-4: IF an exercise name already exists in the catalog (matching case-insensitively and ignoring outer whitespace), THEN THE SYSTEM SHALL reject duplicate creation and report a conflict (HTTP 409).

### Poorly-Written Example (for contrast):

> ~~FR-4: The system must handle duplicate exercises properly and be fast.~~  
> *(No EARS pattern, unverifiable criteria, joins two distinct ideas in one sentence, and contains an unmeasurable adjective).*

## Reviewing an Existing Spec

If the user asks to review rather than create a spec, do not rewrite it immediately. Instead, **detect and list** findings, numbered across four categories:
1. Ambiguities
2. Contradictions between requirements
3. Uncovered edge cases
4. Conflicts with project invariants (documented in `AGENTS.md`)

Do not propose code solutions until explicitly requested.
