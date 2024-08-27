package ir.atefehtaheri.musicbox.core.common

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ir.atefehtaheri.musicbox.R


fun checkPermission(context: Context, permission: String): Boolean {
    return (
            ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED
            )
}

fun launchIntentSender(
    intentSender: IntentSender,
    intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
) {
    intentSenderLauncher.launch(
        IntentSenderRequest.Builder(intentSender).build()
    )
}

fun launchPermissionDialog(
    permissionCallbackLauncher: ActivityResultLauncher<Array<String>>,
    permission: Array<String>,
) {
    permissionCallbackLauncher.launch(permission)
}

fun Fragment.requestIntentSenderCallback(
    onGranted: () -> Unit,
    onDenied: (String) -> Unit,
    context: Context
): ActivityResultLauncher<IntentSenderRequest> {

    return registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onGranted()
        } else {
            onDenied(
                context.getString(R.string.intentSenderDenied)
            )
        }
    }

}

fun Fragment.requestMultiPermissionCallback(
    permissionsGranted: () -> Unit,
    permissionsDenied: (String) -> Unit,
    context: Context,
): ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->

        when {
            permissions.all { it.value } -> {
                permissionsGranted()
            }

            else -> {
                permissionsDenied(
                    context.getString(R.string.permissionsDenied)
                )
            }
        }
    }
}