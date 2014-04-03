package com.temain.printersbook;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;

public class PrinterFilter implements Filter {
	
	private String needle;
	
	public PrinterFilter(String needle) {
		this.needle = needle.toLowerCase();
	}
	
	@Override
	public boolean passesFilter(Object itemId, Item item)
			throws UnsupportedOperationException {
		String haystack = ("" + item.getItemProperty("invent").getValue()
                + item.getItemProperty("podrTitle").getValue() + item
                .getItemProperty("model").getValue()).toLowerCase();
		return haystack.contains(needle);
	}

	@Override
	public boolean appliesToProperty(Object propertyId) {
		return true;
	}

}
