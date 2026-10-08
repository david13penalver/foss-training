import SwiftUI

public struct ContentView: View {
    @Environment(\.theme) private var theme
    @Bindable var appEnvironment: AppEnvironment

    public init(appEnvironment: AppEnvironment) {
        self.appEnvironment = appEnvironment
    }

    public var body: some View {
        TabView {
            WorkoutDashboardView(trainingRepository: appEnvironment.trainingRepository)
                .tabItem {
                    Label("Workouts", systemImage: "flame.fill")
                }

            SessionListView(
                sessionRepository: appEnvironment.sessionRepository,
                trainingRepository: appEnvironment.trainingRepository,
                exerciseRepository: appEnvironment.exerciseRepository
            )
            .tabItem {
                Label("Templates", systemImage: "list.bullet.rectangle.portrait.fill")
            }

            ProgramsListView(
                programRepository: appEnvironment.trainingProgramRepository,
                sessionRepository: appEnvironment.sessionRepository
            )
            .tabItem {
                Label("Programs", systemImage: "calendar.badge.clock")
            }

            ExerciseListView(exerciseRepository: appEnvironment.exerciseRepository)
                .tabItem {
                    Label("Exercises", systemImage: "dumbbell.fill")
                }

            AnalyticsDashboardView(
                trainingRepository: appEnvironment.trainingRepository,
                athleteRepository: appEnvironment.athleteRepository
            )
            .tabItem {
                Label("Analytics", systemImage: "chart.bar.xaxis")
            }

            SettingsView(appEnvironment: appEnvironment)
                .tabItem {
                    Label("Settings", systemImage: "gearshape.fill")
                }
        }
        .tint(theme.selectedAccent.color)
    }
}
