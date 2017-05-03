package ca.ubc.cs.cpsc210.translink.util;

/*
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
*/

/**
 * Compute relationships between points, lines, and rectangles represented by LatLon objects
 */
public class Geometry {
    /**
     * Return true if the point is inside of, or on the boundary of, the rectangle formed by northWest and southeast
     * @param northWest         the coordinate of the north west corner of the rectangle
     * @param southEast         the coordinate of the south east corner of the rectangle
     * @param point             the point in question
     * @return                  true if the point is on the boundary or inside the rectangle
     */


    //WEBCAT:
    /*
    point in rectangle
    point on edge of rectangle
    line in rectangle
    */
    public static boolean rectangleContainsPoint(LatLon northWest, LatLon southEast, LatLon point) {
        double x0 = northWest.getLatitude(); //upperbound
        double x1 = southEast.getLatitude(); //lowerbound
        double x = point.getLatitude();

        double y0 = southEast.getLongitude(); //lowerbound
        double y1 = northWest.getLongitude(); //upperbounnd
        double y = point.getLongitude();

        double lwbx = 0;
        double upbx = 0;
        double lwby = 0;
        double upby = 0;

        if (x0 > x1) {
            lwbx = x1;
            upbx = x0;
        }
        else {
            lwbx = x0;
            upbx = x1;
        }

        if (y0 > y1) {
            lwby = y1;
            upby = y0;
        }
        else {
            lwby = y0;
            upby = y1;
        }

        return x >= lwbx && x <= upbx && y >= lwby && y <= upby;

        //return ((between(x1, x2, x0)) && (between(y1, y2, y0)));
    }

    //HELPERS
    private static int sameSide (double x0, double y0, double x1, double y1,
                                 double px0, double py0, double px1, double py1) {
        int  sameSide = 0;

        double dx  = x1  - x0;
        double dy  = y1  - y0;
        double dx1 = px0 - x0;
        double dy1 = py0 - y0;
        double dx2 = px1 - x1;
        double dy2 = py1 - y1;

        // Cross product of the vector from the endpoint of the line to the point
        double c1 = dx * dy1 - dy * dx1;
        double c2 = dx * dy2 - dy * dx2;

        if (c1 != 0 && c2 != 0)
            sameSide = c1 < 0 != c2 < 0 ? -1 : 1;
        else if (dx == 0 && dx1 == 0 && dx2 == 0)
            sameSide = !between (y0, y1, py0) && !between (y0, y1, py1) ? 1 : 0;
        else if (dy == 0 && dy1 == 0 && dy2 == 0)
            sameSide = !between (x0, x1, px0) && !between (x0, x1, px1) ? 1 : 0;

        return sameSide;
    }

    private static boolean isLineIntersectingLine(Double x0, Double y0, Double x1, Double y1,
                                                  Double x2, Double y2, Double x3, Double y3) {
        int s1 = Geometry.sameSide (x0, y0, x1, y1, x2, y2, x3, y3);
        int s2 = Geometry.sameSide (x2, y2, x3, y3, x0, y0, x1, y1);
        return s1 <= 0 && s2 <= 0;
    }
    //HELPERS


    /**
     * Return true if the rectangle intersects the line
     * @param northWest         the coordinate of the north west corner of the rectangle
     * @param southEast         the coordinate of the south east corner of the rectangle
     * @param src               one end of the line in question
     * @param dst               the other end of the line in question
     * @return                  true if any point on the line is on the boundary or inside the rectangle
     */



    public static boolean rectangleIntersectsLine(LatLon northWest, LatLon southEast, LatLon src, LatLon dst) {

        double latSRC = src.getLatitude();
        double lonSRC = src.getLongitude();

        double latDST = dst.getLatitude();
        double lonDST = dst.getLongitude();

        double x0 = northWest.getLatitude();
        double y0 = northWest.getLongitude();

        double x1 = southEast.getLatitude();
        double y1 = southEast.getLongitude();

        double height = (y0 - y1);
        double width = (x1 - x0);


        if (Geometry.rectangleContainsPoint(northWest, southEast, src) ||
                Geometry.rectangleContainsPoint(northWest, southEast, dst))
            return true;

        /*
        Rectangle2D.Double rectangle = new Rectangle2D.Double(x0, y0, width, height);
        Line2D.Double line = new Line2D.Double(latSRC, lonSRC, latDST, lonDST);
        return line.intersects(rectangle);
        */

        // Check against top rectangle line
        if (Geometry.isLineIntersectingLine (latSRC, lonSRC, latDST, lonDST, x0, y0, x1, y0))
            return true;

        // Check against left rectangle line
        if (Geometry.isLineIntersectingLine (latSRC, lonSRC, latDST, lonDST, x0, y0, x0, y1))
            return true;

        // Check against bottom rectangle line
        if (Geometry.isLineIntersectingLine (latSRC, lonSRC, latDST, lonDST, x0, y1, x1, y1))
            return true;

        // Check against right rectangle line
        if (Geometry.isLineIntersectingLine (latSRC, lonSRC, latDST, lonDST, x1, y0, x1, y1))
            return true;

        return false;

    }

    //methods from http://geosoft.no/software/geometry/Geometry.java.html


    /**
     * A utility method that you might find helpful in implementing the two previous methods
     * Return true if x is >= lwb and <= upb
     * @param lwb      the lower boundary
     * @param upb      the upper boundary
     * @param x         the value in question
     * @return          true if x is >= lwb and <= upb
     */
    private static boolean between(double lwb, double upb, double x) {
        return lwb <= x && x <= upb;
    }
}
