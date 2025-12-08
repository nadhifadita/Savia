package com.example.savia_finalproject.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun SaviaOutlinedField(
    label: String,
    placeholder: String = "",
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        readOnly = readOnly,
        shape = RoundedCornerShape(10.dp),
        textStyle = TextStyle(fontSize = 15.sp, color = Color.Black),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF0066FF),
            unfocusedBorderColor = Color(0xFFE5E7EB)
        )
    )
}

