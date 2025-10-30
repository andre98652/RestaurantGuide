package com.example.restaurantguide.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.restaurantguide.ui.theme.OnPrimary
import com.example.restaurantguide.ui.theme.RedPrimary

@Composable
fun CategoryChips(
    categories: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row {
        categories.forEachIndexed { i, cat ->
            val isSelected = selected.equals(cat, ignoreCase = true)
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(cat) },
                label = { Text(cat) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RedPrimary,
                    selectedLabelColor = OnPrimary
                )
            )
            if (i != categories.lastIndex) Spacer(Modifier.width(8.dp))
        }
    }
}
