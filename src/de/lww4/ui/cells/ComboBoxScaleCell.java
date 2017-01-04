package de.lww4.ui.cells;

import de.lww4.logic.models.scale.Scale;
import javafx.scene.control.cell.ComboBoxListCell;

public class ComboBoxScaleCell extends ComboBoxListCell<Scale>
{	
	@Override
	public void updateItem(Scale item, boolean empty)
	{
		super.updateItem(item, empty);

		if( ! empty)
		{	
			setText(item.getName());
		}
		else
		{
			setText(null);
		}
	}
}