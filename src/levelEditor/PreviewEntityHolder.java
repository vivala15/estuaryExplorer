package levelEditor;

import controller.MainController;
import entities.Entity;


/**
 * Holds the entity that is under edit - whether it be selected from the map or created from the 
 * level editor window. Handles insertion/deletion from the world. Entity is inserted into world when constructed...
 * @author chris
 *
 */
public class PreviewEntityHolder {

	private Entity previewEntity;
	private MainController mc;
	
	public PreviewEntityHolder(Entity entity, MainController mccont){
		this.mc= mccont;
		previewEntity = entity;
	}
	
	public Entity getEntity(){
		return this.previewEntity;
	}
	
	/**
	 * Super bad and slow - should rewrite worlds with hashes instead of lists...
	 */
	public void deleteEntity(){
		previewEntity.deleteSelfFromWorld(mc);
	}
	
}
