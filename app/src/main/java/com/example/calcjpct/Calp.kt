package com.example.calcjpct

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calcjpct.ui.theme.CalcJPCTTheme
import com.example.calcjpct.ui.theme.Purple200
import com.example.calcjpct.ui.theme.textColor
import kotlin.math.pow
import kotlin.math.sqrt

// ------------------------------------------------------------------------------------
// Funciones Utilitarias
// ------------------------------------------------------------------------------------

/**
 * Verifica si la cadena [input] es un número válido (entero o decimal).
 */
fun isNumeric(input: String): Boolean {
    return input.matches("-?[0-9]+(\\.[0-9]+)?".toRegex())
}

// ------------------------------------------------------------------------------------
// Componentes de la Calculadora
// ------------------------------------------------------------------------------------

/**
 * Botón personalizado de la calculadora.
 *
 * @param label Etiqueta o valor que muestra el botón.
 * @param currentText Texto actual de la pantalla.
 * @param previousText Valor anterior utilizado para operaciones.
 * @param operator Operador actual.
 * @param isNewOperation Bandera que indica si se iniciará una nueva operación.
 * @param onTextChange Callback para actualizar el texto en la pantalla.
 * @param onNewOperationChange Callback para actualizar la bandera de nueva operación.
 * @param onOperatorChange Callback para actualizar el operador actual.
 * @param onPreviousTextChange Callback para actualizar el valor previo.
 * @param modifier Modificador para personalizar el botón.
 */
@Composable
fun CalcButton(
    label: String,
    currentText: String,
    previousText: String,
    operator: String,
    isNewOperation: Boolean,
    onTextChange: (String) -> Unit,
    onNewOperationChange: (Boolean) -> Unit,
    onOperatorChange: (String) -> Unit,
    onPreviousTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Caja que actúa como botón estilizado
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0xFF00BCD4))
            .border(0.5.dp, Color(0xFF2C2F32))
            .clickable {
                // Llamada a la función que maneja la acción del botón
                handleButtonClick(
                    label = label,
                    currentText = currentText,
                    previousText = previousText,
                    operator = operator,
                    isNewOperation = isNewOperation,
                    onTextChange = onTextChange,
                    onNewOperationChange = onNewOperationChange,
                    onOperatorChange = onOperatorChange,
                    onPreviousTextChange = onPreviousTextChange
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Texto del botón
        Text(
            text = label,
            style = TextStyle(
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = textColor,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)
        )
    }
}

/**
 * Maneja la acción de cada botón de la calculadora según su [label].
 *
 * Actualiza el estado de la calculadora llamando a los callbacks correspondientes.
 */
private fun handleButtonClick(
    label: String,
    currentText: String,
    previousText: String,
    operator: String,
    isNewOperation: Boolean,
    onTextChange: (String) -> Unit,
    onNewOperationChange: (Boolean) -> Unit,
    onOperatorChange: (String) -> Unit,
    onPreviousTextChange: (String) -> Unit
) {
    when (label) {
        in "0".."9" -> {
            // Si se presiona un dígito, lo añade o reemplaza el texto actual
            val newText = if (isNewOperation || currentText == "0") label else currentText + label
            onTextChange(newText)
            onNewOperationChange(false)
        }
        "+", "-", "*", "/", "%", "^" -> {
            // Guarda el operador y el valor actual para la operación
            onOperatorChange(label)
            onPreviousTextChange(currentText)
            onNewOperationChange(true)
        }
        "AC" -> {
            // Reinicia la calculadora
            onTextChange("0")
            onNewOperationChange(true)
        }
        "." -> {
            // Agrega el punto decimal, evitando duplicados
            val newText = if (isNewOperation) "" else currentText
            if (!newText.contains(".")) {
                onTextChange(newText + ".")
            }
            onNewOperationChange(false)
        }
        "√" -> {
            // Calcula la raíz cuadrada del número actual
            currentText.toDoubleOrNull()?.let {
                val result = sqrt(it)
                onTextChange(result.toString())
                onNewOperationChange(true)
            }
        }
        "1/x" -> {
            // Calcula el inverso del número actual
            currentText.toDoubleOrNull()?.let {
                if (it != 0.0) {
                    onTextChange((1 / it).toString())
                } else {
                    onTextChange("Error")
                }
                onNewOperationChange(true)
            }
        }
        "π" -> {
            // Muestra el valor de π
            onTextChange(Math.PI.toString())
        }
        "=" -> {
            // Realiza la operación matemática según el operador seleccionado
            val operand1 = previousText.toDoubleOrNull()
            val operand2 = currentText.toDoubleOrNull()
            if (operand1 != null && operand2 != null) {
                val result = when (operator) {
                    "*" -> operand1 * operand2
                    "/" -> operand1 / operand2
                    "+" -> operand1 + operand2
                    "-" -> operand1 - operand2
                    "^" -> operand1.pow(operand2)
                    "%" -> operand1 % operand2
                    else -> operand2
                }
                onTextChange(result.toString())
                onNewOperationChange(true)
            }
        }
    }
}

/**
 * Pantalla de visualización de la calculadora.
 *
 * Se utiliza un TextField de solo lectura para mostrar el valor actual.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorDisplay(
    text: String,
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier.fillMaxSize(),
        textStyle = TextStyle(
            fontSize = 36.sp,
            textAlign = TextAlign.End,
            color = textColor
        ),
        maxLines = 2,
        readOnly = true
    )
}

/**
 * Fila de botones de la calculadora.
 *
 * Organiza los botones de una misma fila, distribuyéndolos uniformemente.
 */
@Composable
fun CalculatorRow(
    buttons: List<String>,
    currentText: String,
    previousText: String,
    operator: String,
    isNewOperation: Boolean,
    onTextChange: (String) -> Unit,
    onNewOperationChange: (Boolean) -> Unit,
    onOperatorChange: (String) -> Unit,
    onPreviousTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        buttons.forEach { label ->
            // Cada botón ocupa un peso igual en la fila
            CalcButton(
                label = label,
                currentText = currentText,
                previousText = previousText,
                operator = operator,
                isNewOperation = isNewOperation,
                onTextChange = onTextChange,
                onNewOperationChange = onNewOperationChange,
                onOperatorChange = onOperatorChange,
                onPreviousTextChange = onPreviousTextChange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Vista completa de la calculadora.
 *
 * Incluye la pantalla de visualización y todas las filas de botones organizadas.
 */
@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    CalcJPCTTheme {
        // Estados de la calculadora
        var operator by remember { mutableStateOf("") }
        var isNewOperation by remember { mutableStateOf(true) }
        var previousText by remember { mutableStateOf("") }
        var currentText by remember { mutableStateOf("0") }

        Column(modifier = Modifier.fillMaxSize()) {
            // Pantalla de visualización dentro de un Box estilizado
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .background(Purple200)
            ) {
                CalculatorDisplay(
                    text = currentText,
                    onTextChange = { currentText = it },
                    modifier = Modifier.fillMaxSize()
                )
            }
            // Lista de filas con botones
            val rows = listOf(
                listOf("AC", ".", "%", "/", "√"),
                listOf("7", "8", "9", "*", "^"),
                listOf("4", "5", "6", "+", "1/x"),
                listOf("1", "2", "3", "-", "π"),
                listOf("0", "=")
            )
            // Se renderiza cada fila de botones
            rows.forEach { rowButtons ->
                CalculatorRow(
                    buttons = rowButtons,
                    currentText = currentText,
                    previousText = previousText,
                    operator = operator,
                    isNewOperation = isNewOperation,
                    onTextChange = { currentText = it },
                    onNewOperationChange = { isNewOperation = it },
                    onOperatorChange = { operator = it },
                    onPreviousTextChange = { previousText = it },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
