package br.studyleague.ui.components

import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTime: LocalTime = LocalTime.now(),
    layoutType: TimePickerLayoutType = TimePickerDefaults.layoutType(),
    onDismissRequest: () -> Unit,
    onDone: (LocalTime) -> Unit
) {
    val timePickerState = rememberTimePickerState(initialTime.hour, initialTime.minute)

    DefaultDialog(onDismissRequest = onDismissRequest) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 10.dp, bottom = 10.dp)
        ) {
            TimePicker(
                state = timePickerState,
                layoutType = layoutType,
                colors = TimePickerDefaults.colors()
            )

            TextButton(onClick = {
                onDone(
                    LocalTime.of(
                        timePickerState.hour, timePickerState.minute
                    )
                )
            }) {
                Text("Pronto", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun ViewTimePickerDialog(
    initialTime: LocalTime = LocalTime.now(), onDone: (LocalTime) -> Unit
) {
    val context = LocalContext.current
    val timePickerDialog = TimePickerDialog(
        context, { _, hour, minute ->
            Log.d("TimePickerDialog", "hour: $hour, minute: $minute")
            onDone(LocalTime.of(hour, minute))
        }, initialTime.hour, initialTime.minute, true
    )

    timePickerDialog.show()
}

@Composable
fun DefaultDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            modifier = modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            content()
        }
    }
}