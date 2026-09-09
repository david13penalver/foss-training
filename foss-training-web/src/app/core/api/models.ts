import type { components, paths, operations } from './api-types';

export type Schemas = components['schemas'];

// Core Entity Models
export type Exercise = Schemas['Exercise'];
export type ExerciseRequest = Schemas['ExerciseRequest'];

export type Session = Schemas['Session'];
export type SessionRequest = Schemas['SessionRequest'];
export type SessionExercise = Schemas['SessionExercise'];
export type ResistanceSessionExercise = Schemas['ResistanceSessionExercise'];
export type ResistanceSet = Schemas['ResistanceSet'];
export type EnduranceSessionExercise = Schemas['EnduranceSessionExercise'];
export type MobilitySessionExercise = Schemas['MobilitySessionExercise'];

export type Training = Schemas['Training'];
export type TrainingRequest = Schemas['TrainingRequest'];

export type TrainingProgram = Schemas['TrainingProgramResponse'];
export type TrainingProgramRequest = Schemas['TrainingProgramRequest'];
export type ProgramWorkout = Schemas['ProgramWorkoutResponse'];
export type ProgramWorkoutRequest = Schemas['ProgramWorkoutRequest'];

// Analytics Models
export type OneRepMaxResponse = Schemas['OneRepMaxResponse'];
export type PersonalRecordResponse = Schemas['PersonalRecordResponse'];

// Re-export full OpenAPI spec types
export type { components, paths, operations };
