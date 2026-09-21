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
export type CompleteTrainingRequest = Schemas['CompleteTrainingRequest'];
export type LogSetRequest = Schemas['LogSetRequest'];
export type LogIntervalRequest = Schemas['LogIntervalRequest'];
export type WorkoutSummaryResponse = Schemas['WorkoutSummaryResponse'];
export type WorkoutExerciseSummary = Schemas['WorkoutExerciseSummary'];

export type TrainingProgram = Schemas['TrainingProgramResponse'];
export type TrainingProgramRequest = Schemas['TrainingProgramRequest'];
export type ProgramWorkout = Schemas['ProgramWorkoutResponse'];
export type ProgramWorkoutRequest = Schemas['ProgramWorkoutRequest'];

// Periodization Enums
export type PeriodizationType =
  | 'LINEAR'
  | 'BLOCK'
  | 'UNDULATING'
  | 'REVERSE_LINEAR';

export type ProgramLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'ELITE';

// Analytics Models & Types
export type WeightUnit = 'KG' | 'LBS';

export type OneRepMaxFormula =
  | 'EPLEY'
  | 'BRZYCKI'
  | 'LANDER'
  | 'LOMBARDI'
  | 'MAYHEW'
  | 'OCONNER'
  | 'WATHEN';

export interface Calculate1RmParams {
  weight: number;
  reps: number;
  unit?: WeightUnit;
  formula?: OneRepMaxFormula;
}

export type OneRepMaxResponse = Schemas['OneRepMaxResponse'];
export type PersonalRecordResponse = Schemas['PersonalRecordResponse'];
export type BestEstimated1RmRecord = Schemas['BestEstimated1RmRecord'];
export type MaxWeightRecord = Schemas['MaxWeightRecord'];
export type MaxRepsRecord = Schemas['MaxRepsRecord'];
export type MaxSessionVolumeRecord = Schemas['MaxSessionVolumeRecord'];

// Sports Science Analytics Models
export type AcwrResponse = Schemas['AcwrResponse'];
export type DailyWorkloadResponse = Schemas['DailyWorkloadResponse'];
export type AcwrRiskZone = NonNullable<AcwrResponse['riskZone']>;

export type WeeklyMuscleVolumeResponse = Schemas['WeeklyMuscleVolumeResponse'];
export type MuscleGroupVolume = Schemas['MuscleGroupVolume'];
export type HypertrophyVolumeStatus = NonNullable<MuscleGroupVolume['status']>;

export type ExerciseProgressionResponse = Schemas['ExerciseProgressionResponseDto'];
export type ProgressionDataPoint = Schemas['ProgressionDataPointDto'];
export type ProgressionTrend = NonNullable<ExerciseProgressionResponse['trend']>;

export interface ExerciseProgressionParams {
  exerciseId: number;
  startDate?: string;
  endDate?: string;
  formula?: OneRepMaxFormula;
}

export type HeartRateZonesResponse = Schemas['HeartRateZonesResponseDto'];
export type CalculatedHeartRateZone = Schemas['CalculatedHeartRateZoneDto'];
export type HeartRateZoneMethod = NonNullable<HeartRateZonesResponse['method']>;

export interface CalculateHeartRateZonesParams {
  maxHr?: number;
  restingHr?: number;
  age?: number;
}

// Re-export full OpenAPI spec types
export type { components, paths, operations };
