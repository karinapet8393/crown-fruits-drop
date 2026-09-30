package com.WqNzVmK.rJpLtF.presentation.recipes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.WqNzVmK.rJpLtF.R
import com.WqNzVmK.rJpLtF.databinding.ItemRecipeBinding

/** Renders the four dishes of the royal menu with their unlock badge. */
class RecipeAdapter : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private val items = mutableListOf<RecipeListItem>()

    fun submit(newItems: List<RecipeListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class RecipeViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RecipeListItem) {
            val context = binding.root.context
            binding.itemName.text = context.getString(item.recipe.nameResId)

            val done = item.state == RecipeListItem.State.DONE
            val collectedFirst = if (done) item.recipe.firstTarget else 0
            val collectedSecond = if (done) item.recipe.secondTarget else 0
            binding.itemStrip.render(item.recipe, collectedFirst, collectedSecond)

            val badgeRes = when (item.state) {
                RecipeListItem.State.DONE -> R.string.badge_done
                RecipeListItem.State.NEXT -> R.string.badge_next
                RecipeListItem.State.LOCKED -> R.string.badge_locked
            }
            val stateRes = when (item.state) {
                RecipeListItem.State.DONE -> R.string.state_done
                RecipeListItem.State.NEXT -> R.string.state_next
                RecipeListItem.State.LOCKED -> R.string.state_locked
            }
            val accentRes = when (item.state) {
                RecipeListItem.State.DONE -> R.color.green
                RecipeListItem.State.NEXT -> R.color.gold
                RecipeListItem.State.LOCKED -> R.color.cream_dim
            }

            binding.itemBadge.setText(badgeRes)
            binding.itemBadge.setTextColor(context.getColor(accentRes))
            binding.itemCard.alpha =
                if (item.state == RecipeListItem.State.LOCKED) LOCKED_ALPHA else 1f
            ViewCompat.setStateDescription(binding.itemCard, context.getString(stateRes))
        }

        private companion object {
            const val LOCKED_ALPHA = 0.55f
        }
    }
}
