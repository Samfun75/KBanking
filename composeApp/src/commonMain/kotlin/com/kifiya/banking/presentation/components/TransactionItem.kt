package com.kifiya.banking.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kifiya.banking.domain.model.Transaction
import com.kifiya.banking.domain.model.TransactionDirection
import com.kifiya.banking.domain.model.TransactionType
import com.kifiya.banking.presentation.theme.CreditGreen
import com.kifiya.banking.presentation.theme.DebitRed
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun TransactionItem(
    transaction: Transaction,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (transaction.direction == TransactionDirection.CREDIT)
                            CreditGreen.copy(alpha = 0.1f)
                        else
                            DebitRed.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = transaction.type.icon(),
                    contentDescription = transaction.type.displayName(),
                    modifier = Modifier.size(24.dp),
                    tint = if (transaction.direction == TransactionDirection.CREDIT)
                        CreditGreen
                    else
                        DebitRed
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = transaction.type.displayName(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatTimestamp(transaction.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (transaction.direction == TransactionDirection.CREDIT) "+" else "-"}${formatCurrency(transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (transaction.direction == TransactionDirection.CREDIT)
                        CreditGreen
                    else
                        DebitRed
                )
                Text(
                    text = transaction.direction.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun TransactionType.displayName(): String = when (this) {
    TransactionType.TRANSFER -> "Transfer"
    TransactionType.DEPOSIT -> "Deposit"
    TransactionType.WITHDRAWAL -> "Withdrawal"
    TransactionType.PAYMENT -> "Payment"
}

private fun TransactionType.icon(): ImageVector = when (this) {
    TransactionType.TRANSFER -> Icons.AutoMirrored.Filled.CompareArrows
    TransactionType.DEPOSIT -> Icons.Default.Savings
    TransactionType.WITHDRAWAL -> Icons.Default.AccountBalanceWallet
    TransactionType.PAYMENT -> Icons.Default.Payment
}

private fun formatTimestamp(instant: Instant): String {
    return try {
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${dateTime.month.name.take(3)} ${dateTime.dayOfMonth}, ${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
    } catch (e: Exception) {
        "Unknown date"
    }
}
