package com.asurspace.vehicledata_boundsofhamburg.common.utils

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.lifecycle.viewModelScope
import com.asurspace.vehicledata_boundsofhamburg.R
import com.asurspace.vehicledata_boundsofhamburg.presentation.ui.map.viewmodel.MapVehicleViewVM
import kotlinx.coroutines.launch


@Composable
fun ErrorDialog(@DrawableRes resId: Int, @StringRes message: Int, viewModel: MapVehicleViewVM) {
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Row {
                    Icon(
                        painter = painterResource(id = resId),
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.error),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            },
            text = {
                Text(stringResource(id = message), modifier = Modifier.padding(8.dp))

            },
            confirmButton = {
                Button(modifier = Modifier.padding(8.dp), onClick = {
                    viewModel.viewModelScope.launch {
                        viewModel.fetchByCoordinate()
                    }
                    openDialog.value = false
                }, content = {
                    Text(text = "Try again.")
                })
            }
        )
    }
}