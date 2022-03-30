package com.asurspace.vehicledata_boundsofhamburg.utils

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asurspace.vehicledata_boundsofhamburg.MainVM
import com.asurspace.vehicledata_boundsofhamburg.R



@Composable
fun ErrorDialog(message: String) {
    val viewModel: MainVM = viewModel()
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                //openDialog.value
            },
            title = {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_out_connection_24),
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.error),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            },
            text = {
                Text(message, modifier = Modifier.padding(8.dp))

            },
            confirmButton = {
                Button(modifier = Modifier.padding(8.dp), onClick = {
                    viewModel.fetchPoiByCoordinates()
                    openDialog.value = false
                }, content = {
                    Text(text = "Try again.")
                })
            }
        )
    }
}