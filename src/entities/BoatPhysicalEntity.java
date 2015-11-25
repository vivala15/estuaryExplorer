package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import terrains.Terrain;

public abstract class BoatPhysicalEntity extends PhysicalEntity {
	
	private Vector3f buoncyForce = new Vector3f();
	private Vector3f waterDragForce = new Vector3f();
	private float baseBuoncyForce;
	private float waterDragCoeff = 100.0f;
	
	public BoatPhysicalEntity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale,
			float mass) {
		super(model, position, rotX, rotY, rotZ, scale, mass);
		baseBuoncyForce = -1.00f*mass*this.GRAVITY;
		buoncyForce.set(0f, baseBuoncyForce, 0f);
	}

	public void resolveTerrainCollision(Terrain t, float waterHeight) {
		float originalY = this.getPhysicalPosition().y;
		float terrainHeight = t.getHeightOfTerrain(this.getPhysicalPosition().getX(),
				this.getPhysicalPosition().getZ());
		
		float surfaceHeight  = 
				Math.max(this.getPhysicalPosition().y, 
						Math.max(terrainHeight,waterHeight));
		
		
		//beneath the top surface
		if(originalY < surfaceHeight ){
			//above ground , below water
			if(originalY > terrainHeight){
				//based on above booleans - this should always be a positive value
				this.applyBuoncy(surfaceHeight - originalY);
			}else{
				this.getPhysicalPosition().y = terrainHeight;
				this.getVelocity().y = 0.0f;
			}
		}
	}

	/**
	 * Could add physics, but for now keep simple and just apply a little more than 
	 * equivalent gravitational force
	 */
	private void applyBuoncy(float deltaZ) {
		//System.out.println("Apply Buoncy");
		buoncyForce.y = deltaZ * baseBuoncyForce;
		waterDragForce.y = -this.getVelocity().y * this.waterDragCoeff;
		this.applyExternalForce(waterDragForce);
		this.applyExternalForce(buoncyForce);
		
	}

}
