import type { components, paths, operations } from './api-types';

export type Schemas = components['schemas'];

// Core Entity Models
export type Exercise = Schemas['Exercise'];
export type ExerciseRequest = Schemas['ExerciseRequest'];
export type PageResponseExercise = Schemas['PageResponseDtoExercise'];

export interface ExerciseSearchParams {
  search?: string;
  q?: string;
  primaryCategory?: string;
  muscleGroup?: string;
  equipment?: string;
  difficultyLevel?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
}

export type Session = Schemas['Session'];
export type SessionRequest = Schemas['SessionRequest'];
export type CloneSessionRequest = Schemas['CloneSessionRequest'];
export type SessionExercise = Schemas['SessionExercise'];
export type ResistanceSessionExercise = Schemas['ResistanceSessionExercise'];
export type ResistanceSet = Schemas['ResistanceSet'];
export type EnduranceSessionExercise = Schemas['EnduranceSessionExercise'];
export type MobilitySessionExercise = Schemas['MobilitySessionExercise'];

export type Training = Schemas['Training'];
export type TrainingRequest = Schemas['TrainingRequest'];
export type PageResponseTraining = Schemas['PageResponseDtoTraining'];

export interface TrainingSearchParams {
  startDate?: string;
  endDate?: string;
  status?: string;
  search?: string;
  q?: string;
  programId?: number;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
}
export type CompleteTrainingRequest = Schemas['CompleteTrainingRequest'];
export type LogSetRequest = Schemas['LogSetRequest'];
export type LogIntervalRequest = Schemas['LogIntervalRequest'];
export type WorkoutSummaryResponse = Schemas['WorkoutSummaryResponse'];
export type WorkoutExerciseSummary = Schemas['WorkoutExerciseSummary'];

export type TrainingProgram = Schemas['TrainingProgramResponse'];
export type TrainingProgramRequest = Schemas['TrainingProgramRequest'];
export type CloneProgramRequest = Schemas['CloneProgramRequest'];
export type ProgramWorkout = Schemas['ProgramWorkoutResponse'];
export type ProgramWorkoutRequest = Schemas['ProgramWorkoutRequest'];

// Program Adherence & Periodization Models
export type ProgramAdherenceResponse = Schemas['ProgramAdherenceResponse'];
export type WeeklyAdherence = Schemas['WeeklyAdherence'];
export type WorkoutAdherenceItem = Schemas['WorkoutAdherenceItem'];
export type ProgramAdherenceStatus = NonNullable<ProgramAdherenceResponse['status']>;

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

// Athlete & Anthropometric Models
export type BodyweightEntry = Schemas['BodyweightResponse'];
export type BodyweightRequest = Schemas['BodyweightRequest'];
export type RelativeStrengthRequest = Schemas['RelativeStrengthRequest'];
export type RelativeStrengthResponse = Schemas['RelativeStrengthResponse'];
export type AthleteGender = 'MALE' | 'FEMALE';

// Data Sovereignty & Portability Models
export type FullBackupData = Schemas['FullBackupData'];
export type ImportSummary = Schemas['ImportSummary'];

// Re-export full OpenAPI spec types
export type { components, paths, operations };

