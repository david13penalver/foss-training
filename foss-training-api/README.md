# FOSS Training API

The backend API for the FOSS Training project, designed to manage physical training sessions, exercises, and training plans.

## Tech Stack

- **Java**: 21
- **Framework**: Spring Boot 4.0.1
- **Database**: MariaDB (Production), H2 (Test)
- **Dependencies**:
  - Spring Boot WebMVC
  - Spring Boot Data JPA
  - Lombok

## Purpose

This API serves as the core backend for the FOSS Training ecosystem, providing REST endpoints to handle data persistence and business logic for training management.

## Domain Model

The API implements a comprehensive domain model for physical training:

### Core Entities
- **Exercise**: Represents a physical activity with detailed metadata (instructions, difficulty, video, etc.).
- **Metrics**: Polymorphic metrics support for different exercise types:
  - `ResistanceMetrics`: For weight training (reps, sets, weight, etc.).
  - `EnduranceMetrics`: For cardio activities (distance, duration, heart rate, etc.).
  - `MobilityMetrics`: For flexibility and mobility work (hold time, stretch type, etc.).

### Classifications
- **Categories**: Resistance, Endurance, Mobility, Plyometrics, etc.
- **Muscle Groups**: Detailed muscle targeting (primary/secondary) including categories (Push, Pull, Legs, Core).
- **Movement Patterns**: Functional classification (Push, Pull, Squat, Hinge, Lunge, etc.).
- **Equipment**: Extensive equipment list categorized by type (Free Weights, Machines, Cardio, etc.).
- **Difficulty**: Progressive levels (Beginner to Expert).
