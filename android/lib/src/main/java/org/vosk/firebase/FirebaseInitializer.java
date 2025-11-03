package org.vosk.firebase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.vosk.BuildConfig;
import org.vosk.R;

import java.util.List;

/**
 * Helper responsible for initializing and configuring the Firebase services that the
 * library relies on. The consumer application should ensure that the default FirebaseApp
 * is registered either through google-services.json or a manual Firebase options
 * configuration prior to using the library APIs that touch Firebase.
 */
public final class FirebaseInitializer {

    private static final String TAG = "VoskFirebase";
    private static volatile boolean initialized;

    private FirebaseInitializer() {
        // Utility class.
    }

    /**
     * Initializes Crashlytics, Performance Monitoring and Remote Config.
     * The method is idempotent and can be safely called multiple times.
     */
    public static void initialize(@NonNull Context context) {
        if (initialized) {
            return;
        }

        ensureFirebaseApp(context);

        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCrashlyticsCollectionEnabled(true);

        FirebasePerformance performance = FirebasePerformance.getInstance();
        performance.setPerformanceCollectionEnabled(true);

        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(BuildConfig.DEBUG ? 0 : 3600)
                .build();
        remoteConfig.setConfigSettingsAsync(settings);
        remoteConfig.setDefaultsAsync(R.xml.firebase_remote_config_defaults);

        initialized = true;
    }

    private static void ensureFirebaseApp(@NonNull Context context) {
        List<FirebaseApp> apps = FirebaseApp.getApps(context);
        if (!apps.isEmpty()) {
            return;
        }

        FirebaseApp app = FirebaseApp.initializeApp(context);
        if (app == null) {
            Log.w(TAG, "Failed to initialize default FirebaseApp. " +
                    "Ensure google-services.json is present or Firebase options are provided.");
        }
    }
}
