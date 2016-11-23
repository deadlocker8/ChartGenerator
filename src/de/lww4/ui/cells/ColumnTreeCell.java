package de.lww4.ui.cells;

import de.lww4.logic.ColumnTreeItem;
import javafx.scene.control.TreeCell;

public class ColumnTreeCell extends TreeCell<ColumnTreeItem>
{		
	@Override
	protected void updateItem(ColumnTreeItem item, boolean empty)
	{
		super.updateItem(item, empty);

		if( ! empty)
		{			
			setText(item.getText());			
		}
		else
		{
			setText(null);
		}
	}
}