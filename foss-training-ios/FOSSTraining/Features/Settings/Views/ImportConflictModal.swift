import SwiftUI

public struct ImportConflictModal: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.theme) private var theme

    @Binding var selectedMode: ImportMode
    let onConfirm: (ImportMode) -> Void

    public init(
        selectedMode: Binding<ImportMode>,
        onConfirm: @escaping (ImportMode) -> Void
    ) {
        self._selectedMode = selectedMode
        self.onConfirm = onConfirm
    }

    public var body: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: 20) {
                VStack(alignment: .leading, spacing: 6) {
                    Text("Resolve Conflicts")
                        .font(.title2.weight(.bold))
                    Text("Select how to handle records with colliding IDs between your local database and the backup snapshot.")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }

                VStack(spacing: 12) {
                    ForEach(ImportMode.allCases) { mode in
                        Button {
                            selectedMode = mode
                        } label: {
                            HStack(alignment: .top, spacing: 14) {
                                Image(systemName: selectedMode == mode ? "record.circle.fill" : "circle")
                                    .font(.title3)
                                    .foregroundStyle(selectedMode == mode ? theme.selectedAccent.color : .secondary)

                                VStack(alignment: .leading, spacing: 4) {
                                    Text(mode.displayName)
                                        .font(.headline)
                                        .foregroundStyle(Color.primary)
                                    Text(mode.description)
                                        .font(.caption)
                                        .foregroundStyle(.secondary)
                                }
                                Spacer()
                            }
                            .padding()
                            .background(
                                RoundedRectangle(cornerRadius: 12)
                                    .fill(selectedMode == mode ? theme.selectedAccent.color.opacity(0.12) : Color(.secondarySystemBackground))
                            )
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .strokeBorder(selectedMode == mode ? theme.selectedAccent.color : Color.clear, lineWidth: 1.5)
                            )
                        }
                        .buttonStyle(.plain)
                    }
                }

                Spacer()

                Button {
                    onConfirm(selectedMode)
                    dismiss()
                } label: {
                    Text("Proceed with Import")
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(theme.selectedAccent.color)
                        .foregroundStyle(theme.selectedAccent.badgeTextColor)
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                }
            }
            .padding()
            .navigationTitle("Import Options")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") { dismiss() }
                }
            }
        }
    }
}
