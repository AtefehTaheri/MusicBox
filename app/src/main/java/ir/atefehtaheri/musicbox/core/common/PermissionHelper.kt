package ir.atefehtaheri.musicbox.core.common

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


fun checkPermission(context: Context, permission: String): Boolean {
    return (
            ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED
            )
}

fun launchPermissionDialog(
    permissionCallbackLauncher: ActivityResultLauncher<Array<String>>,
    permission: Array<String>,
) {
    permissionCallbackLauncher.launch(permission)
}

fun Fragment.requestMultiPermissionCallback(
    permissionsGranted: () -> Unit,
    permissionsDenied: () -> Unit,
): ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->

        when {
            permissions.all { it.value } -> {
                permissionsGranted()
            }
            else -> {
                permissionsDenied()
            }
        }
    }
}