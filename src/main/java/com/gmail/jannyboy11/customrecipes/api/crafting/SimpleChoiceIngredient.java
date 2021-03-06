package com.gmail.jannyboy11.customrecipes.api.crafting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gmail.jannyboy11.customrecipes.api.InventoryUtils;
import com.gmail.jannyboy11.customrecipes.api.crafting.vanilla.ingredient.ChoiceIngredient;
import com.gmail.jannyboy11.customrecipes.api.crafting.vanilla.ingredient.EmptyIngredient;

/**
 * Represents an ingredient for crafting recipes that aims to mirror vanilla ingredients. 
 * 
 * @author Jan
 */
public class SimpleChoiceIngredient implements ChoiceIngredient {
	
	private static class SimpleEmptyIngredient extends SimpleChoiceIngredient implements EmptyIngredient {
		private SimpleEmptyIngredient() {
			super(Collections.emptyList());
		}
		/**
		 * Check if the ItemStack is accepted by this ingredient.
		 * @return true if the itemStack is null or air, otherwise false
		 */
		@Override
		public boolean isIngredient(ItemStack itemStack) {
			return InventoryUtils.isEmptyStack(itemStack);
		}
		/**
		 * Get the choices.
		 * @return an empty list
		 */
		@Override
		public List<? extends ItemStack> getChoices() {
			return Collections.emptyList();
		}
	}
	
	/**
	 * The ChoiceIngredient that accepts empty stacks
	 */
	public static final SimpleEmptyIngredient ACCEPTING_EMPTY = new SimpleEmptyIngredient();
	
	private final List<? extends ItemStack> choices;
	
	/**
	 * Instantiate the class using the given choices
	 * @param choices the list of choices
	 */
	public SimpleChoiceIngredient(List<? extends ItemStack> choices) {
		this.choices = Objects.requireNonNull(choices);
	}
	
	/**
	 * Static method for deserialization.
	 * 
	 * @param choicesMap the map containing the choices list for key "choices"
	 * @return a new SimpleChoicesIngredient if there are choices in the map, otherwise an empty ingredient
	 */
	@SuppressWarnings("unchecked")
	public static SimpleChoiceIngredient deserialize(Map<String, Object> choicesMap) {
		List<? extends ItemStack> choices = (List<? extends ItemStack>) choicesMap.get("choices");
		if (choices == null) return ACCEPTING_EMPTY;
		return new SimpleChoiceIngredient(choices);
	}
	
	/**
	 * Static method
	 * 
	 * @param choices the choices
	 * @return a new SimpleChoiceIngredient
	 */
	public static ChoiceIngredient fromChoices(ItemStack... choices) {
		if (choices == null || choices.length == 0) return ACCEPTING_EMPTY;
		return new SimpleChoiceIngredient(Arrays.asList(choices));
	}

	/**
	 * Checks if the item is accepted by this ingredient.
	 * 
	 * @param itemStack the item stack to test
	 * @return true if the itemStack matches any choice, otherwise false
	 */
	@Override
	public boolean isIngredient(ItemStack itemStack) {
		return choices.stream().anyMatch(choice -> {
			if (choice == null) {
				return itemStack == null;
			} else {
				Material choiceMaterial = choice.getType();
				Material stackMaterial = itemStack.getType();
				if (choiceMaterial != stackMaterial) return false;
				
				return choice.getDurability() == -1 || choice.getDurability() == Short.MAX_VALUE || choice.getDurability() == itemStack.getDurability();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<? extends ItemStack> getChoices() {
		return choices;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ChoiceIngredient)) return false;
		ChoiceIngredient that = (ChoiceIngredient) o;
		return Objects.equals(this.getChoices(), that.getChoices());
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(getChoices());
	}
	
	@Override
	public String toString() {
		return getClass().getName() + "{" +
				"choices=" + choices +
				"}";
	}

}
