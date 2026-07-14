package com.cadoodlecad.manifold;

/**
 * Simple 3D bounding box replacement for {@code javafx.geometry.BoundingBox}.
 */
public class BoundingBox3D {
	public final double centerX, centerY, centerZ;
	public final double depth, height, width;
	public final double minX, minY, minZ;
	public final double maxX, maxY, maxZ;

	public BoundingBox3D(double minX, double minY, double minZ, double width, double height, double depth) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.maxX = minX + width;
		this.maxY = minY + height;
		this.maxZ = minZ + depth;
		this.centerX = minX + width / 2.0;
		this.centerY = minY + height / 2.0;
		this.centerZ = minZ + depth / 2.0;
	}

	@Override
	public String toString() {
		return "BoundingBox3D[min=(" + minX + "," + minY + "," + minZ + "), size=(" + width + "," + height + "," + depth + ")]";
	}
}
