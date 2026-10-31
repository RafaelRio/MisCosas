//
//  FirestoreRemoteUserProfileRepository.swift
//  iosApp
//
//  Created by Rafael Río on 31/10/2026.
//

import Foundation
import SharedLogic
import FirebaseFirestore

final class FirestoreRemoteUserProfileRepository: RemoteUserProfileRepository {

    private let firestore: Firestore

    init(firestore: Firestore = Firestore.firestore()) {
        self.firestore = firestore
    }

    func findById(
        userId: UserId,
        completionHandler: @escaping (User?, Error?) -> Void
    ) {
        firestore
            .collection("users")
            .document(userId.value)
            .getDocument(source: .server) { snapshot, error in
                if let error {
                    completionHandler(nil, error)
                    return
                }

                guard let snapshot else {
                    completionHandler(
                        nil,
                        FirestoreRemoteUserProfileRepositoryError.invalidProfile
                    )
                    return
                }

                guard snapshot.exists else {
                    completionHandler(nil, nil)
                    return
                }

                guard
                    let data = snapshot.data(),
                    let displayName = data["displayName"] as? String,
                    let createdAt = data["createdAt"] as? Timestamp,
                    let updatedAt = data["updatedAt"] as? Timestamp
                else {
                    completionHandler(
                        nil,
                        FirestoreRemoteUserProfileRepositoryError.invalidProfile
                    )
                    return
                }

                let user = User(
                    id: userId,
                    displayName: displayName,
                    createdAt: Self.toKotlinInstant(createdAt),
                    updatedAt: Self.toKotlinInstant(updatedAt)
                )

                completionHandler(user, nil)
            }
    }

    func upsert(
        user: User,
        completionHandler: @escaping (Error?) -> Void
    ) {
        let data: [String: Any] = [
            "displayName": user.displayName,
            "createdAt": Self.toFirestoreTimestamp(user.createdAt),
            "updatedAt": Self.toFirestoreTimestamp(user.updatedAt)
        ]

        firestore
            .collection("users")
            .document(user.id.value)
            .setData(data) { error in
                completionHandler(error)
            }
    }

    private static func toKotlinInstant(
        _ timestamp: Timestamp
    ) -> KotlinInstant {
        KotlinInstant.companion.fromEpochSeconds(
            epochSeconds: timestamp.seconds,
            nanosecondAdjustment: timestamp.nanoseconds
        )
    }

    private static func toFirestoreTimestamp(
        _ instant: KotlinInstant
    ) -> Timestamp {
        Timestamp(
            seconds: instant.epochSeconds,
            nanoseconds: instant.nanosecondsOfSecond
        )
    }
}

private enum FirestoreRemoteUserProfileRepositoryError: LocalizedError {
    case invalidProfile

    var errorDescription: String? {
        switch self {
        case .invalidProfile:
            return "El documento remoto no tiene el formato esperado."
        }
    }
}