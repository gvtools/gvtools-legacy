/*
 (read ASCII survey file and produce vector objects)
 - parse a single file or a whole directory of files (import all into one dataset!)
 - robustly parse ASCII file:
   - skip comments
   - skip empty lines
   - skip FS and combinations of FS
   - correctly parse quotation marks
   - deal with different decimal symbols
   - robustly detect coordinate pairs/triplets in the data or
     let user specify structure
 - look for EVENTS in ASCII file:
   - set point
   - start linestring
   - end linestring
   - start island boundary
   - end island boundary
  - start multi-part linestring
   - end multi-part linestring
   - set label point
  - (abort and) delete last point, linestring or polygon
  - ASCII tags for events can be defined in external file
   or taken from a built-in device database
 - export linestrings as lines, polygons as closed polylines
 - handle 2D and 3D input
 - leave it to the user to convert closed (3D!) polylines
   to polygons or triangle meshes
 - do not fiddle with topology
 - save labels as attribute data for all features
 - for closed linestrings: also save a new label centroid as a separate point feature
 - export point ID to
 - automatically eliminate duplicate points/vertices (definable tolerance)
 - allow snapping (definable threshold):
   - snap to point features
   - snap to line features (vertices and edges)
 - output label points to separate layer (or skip)
 - output feature types point and line to separate layers; skip any that are not specified
   (skipping all will simply parse the file without creating data)
 - optionally output deleted features to separate layers
 - produce 2D or 3D output (but always save Z coordinate to attribute table for each vertex)
 - produce verbose diagnostics file
 CAVEATS:
 - How to handle holes in polygons? Export with same cat and then
   let GRASS take care of the building?
 - How to handle multi-part features?
*/

