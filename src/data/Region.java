package data;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Region {

	private RectilinearPixelPoly mPoly;

	private boolean mIsUsed = true;
	private int mDataReadings = 0;
	private Regions mRegionManager;

	public Region(RectilinearPixelPoly polygon, Regions regionManager) {
		mPoly = polygon;
		mPoly.setRegion(this);
		mRegionManager = regionManager;
	}

	protected void addDataReading() {
		++mDataReadings;
	}

	protected void resetDataReadingCount() {
		mDataReadings = 0;
	}

	public int getDataReadingCount() {
		return mDataReadings;
	}

	public boolean getIsUsed() {
		return mIsUsed;
	}

	public void setIsUsed(boolean isIt) {
		mIsUsed = isIt;
	}

	/**
	 * Checks whether this region neighbors the passed region
	 * 
	 * @param r
	 * @return
	 */
	public boolean touches(Region r) {
		return mPoly.touches(((RectilinearPixelPoly) r.getPolyImpl()));
	}

	/**
	 * Returns the underlying polygon. The type of polygon returned will be
	 * dependent on the underlying implementation used
	 * 
	 * @return
	 */
	public Object getPolyImpl() {
		return mPoly;
	}

	/**
	 * Callback method for the polygon implementation to notify this region that
	 * it's underlying polygon has been fully consumed. The typical response is
	 * that this region will remove itself from the region manager and quietly
	 * die
	 */
	public void polygonFullyConsumed() {
		mRegionManager.removeRegion(this);
	}

	/**
	 * Callback method for the polygon implementation to notify this region that
	 * it has been split, and the passed object is the new polygon
	 * implementation that resulted from the split. The typical response to this
	 * callback is for the Region to handle creating/registering a new region
	 * with the region manager
	 * 
	 * @param newPolygonData
	 *            the exact type of data passed will depend upon the polygon
	 *            implementation
	 */
	public void newPolygonCreated(Object newPolygonData) {
		RectilinearPixelPoly poly = (RectilinearPixelPoly) newPolygonData;
		Region newRegion = new Region(poly, mRegionManager);
		mRegionManager.addRegion(newRegion);
	}

	/**
	 * Returns an image useful for debugging this region. The width/height is
	 * equal to the total area available in {@link Regions}, and all pixels are
	 * black or blue, with black indicating pixels not belonging to this region,
	 * and blue indicating pixels belonging to this region
	 * 
	 * @return
	 */
	public BufferedImage getDebugImage() {
		Rectangle mBorder = mRegionManager.getBorderRectangle();
		BufferedImage bi = new BufferedImage(mBorder.width + 1,
				mBorder.height + 1, BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();

		g.setColor(Color.BLUE);
		for (Point p : mPoly.mPoints)
			g.drawLine(p.x, p.y, p.x, p.y);

		return bi;
	}

}
