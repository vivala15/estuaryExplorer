package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import terrains.Terrain;

public abstract class BoatPhysicalEntity extends PhysicalEntity {
	
	
	public BoatPhysicalEntity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale,
			float mass) {
		super(model, position, rotX, rotY, rotZ, scale, mass);
	}

	public void resolveTerrainCollision(Terrain t, float waterHeight) {
		float originalY = this.getPhysicalPosition().y;
		this.getPhysicalPosition().y = 
				Math.max(this.getPhysicalPosition().y, 
						Math.max(
								t.getHeightOfTerrain(this.getPhysicalPosition().getX(),
													this.getPhysicalPosition().getZ()),
								waterHeight));
		
		if(originalY < this.getPhysicalPosition().y){
			this.getVelocity().y = 0.0f;
		}
		
	}

}
