package com.mimikunda.nightwatch.ui.settings.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mimikunda.nightwatch.domain.model.TableShape
import com.mimikunda.nightwatch.ui.theme.TableSurfaceBorder
import com.mimikunda.nightwatch.ui.theme.TableSurfaceCenter
import kotlin.math.cos
import kotlin.math.sin

data class ShapeOption(
    val shape: TableShape,
    val title: String,
    val description: String
)

val TableShapeOptions = listOf(
    ShapeOption(TableShape.RECTANGLE, "Rectangle (Default)", "Four-sided edge seat distribution"),
    ShapeOption(TableShape.CIRCLE, "Circle", "Equal angular seat spacing around a circular table"),
    ShapeOption(TableShape.OVAL, "Oval", "Elongated elliptical seat distribution"),
    ShapeOption(TableShape.HEXAGON, "Hexagon", "6-sided polygon seating geometry"),
    ShapeOption(TableShape.OCTAGON, "Octagon", "8-sided polygon seating geometry")
)

@Composable
fun TableShapePicker(
    selectedShape: TableShape,
    onSelectShape: (TableShape) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TableShapeOptions.forEach { option ->
            val isSelected = selectedShape == option.shape

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectShape(option.shape) },
                shape = RoundedCornerShape(16.dp),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    else Color.DarkGray.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectShape(option.shape) }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    ShapePreviewCanvas(shape = option.shape)

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = option.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = option.description,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShapePreviewCanvas(shape: TableShape) {
    Box(
        modifier = Modifier
            .size(48.dp, 36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black)
            .border(1.dp, TableSurfaceBorder, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val insetX = w * 0.18f
            val insetY = h * 0.18f
            val tw = w - (insetX * 2f)
            val th = h - (insetY * 2f)

            when (shape) {
                TableShape.RECTANGLE -> {
                    drawRoundRect(
                        color = TableSurfaceCenter,
                        topLeft = Offset(insetX, insetY),
                        size = Size(tw, th),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                    drawRoundRect(
                        color = TableSurfaceBorder,
                        topLeft = Offset(insetX, insetY),
                        size = Size(tw, th),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
                TableShape.CIRCLE, TableShape.OVAL -> {
                    drawOval(color = TableSurfaceCenter, topLeft = Offset(insetX, insetY), size = Size(tw, th))
                    drawOval(color = TableSurfaceBorder, topLeft = Offset(insetX, insetY), size = Size(tw, th), style = Stroke(width = 1.dp.toPx()))
                }
                TableShape.HEXAGON, TableShape.OCTAGON -> {
                    val sides = if (shape == TableShape.HEXAGON) 6 else 8
                    val path = Path()
                    val rx = tw / 2f
                    val ry = th / 2f
                    val cx = w / 2f
                    val cy = h / 2f

                    for (i in 0 until sides) {
                        val angle = (2.0 * Math.PI * i / sides) - (Math.PI / 2.0)
                        val x = (cx + rx * cos(angle)).toFloat()
                        val y = (cy + ry * sin(angle)).toFloat()
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    path.close()

                    drawPath(path, color = TableSurfaceCenter)
                    drawPath(path, color = TableSurfaceBorder, style = Stroke(width = 1.dp.toPx()))
                }
            }
        }
    }
}
