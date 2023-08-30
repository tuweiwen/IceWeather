package com.tomastu.iceweather.firstOpen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.tomastu.iceweather.R

private const val TAG = "FirstOpenComponents"
@Composable
fun PrivacyAgreementDialog(
    modifier: Modifier = Modifier,
    onAgreeClick: () -> Unit,
    onDisAgreeClick: () -> Unit,
) {
    AlertDialog(
        modifier = modifier.fillMaxHeight(0.8f),
        onDismissRequest = { },  /* 用户点击外部，不响应 */
        confirmButton = {
            TextButton(onClick = {
                // 用户同意条款，将 “是否首次启动” 记为 false
                onAgreeClick()
                Log.e(TAG, "confirmButton")
            }) {
                Text(text = stringResource(id = R.string.agree))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                // 用户不同意条款，直接退出
                onDisAgreeClick()
                Log.e(TAG, "dismissButton")
            }) {
                Text(text = stringResource(id = R.string.disagree))
            }
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.agreement),
                contentDescription = stringResource(id = R.string.privacyAgreementDialogTitle)
            )
        },
        title = { Text(text = stringResource(id = R.string.privacyAgreementDialogTitle)) },
        containerColor = MaterialTheme.colorScheme.background,
        text = {
            LazyColumn {
                item {
                    Text(
                        text = stringResource(id = R.string.agreementContent),
                    )
                }
            }
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun AgreementDialogPreview() {
    PrivacyAgreementDialog(onAgreeClick = {}, onDisAgreeClick = {})
}