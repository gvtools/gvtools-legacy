/*
(convert 2D/3D polylines or polygons into triangle meshes)
Processing chain:
1. decompose linestrings or polygon boundaries into
   vertices
2. make Delaunay triangulation of (1)
3. write result to a new map
CAVEATS:
- What to do with holes in polygons? Perhaps it would be possible
to eliminate those triangles that represent holes in a 2nd pass.
- Where to save attribute table values? In a new 2D/3D label point or attached
to the first triangle or attached to all triangles.

r.rand.offset
(vary raster data with random local and object-based offsets)
- input: raster map an (optionally) vector map
- vary values of ALL cells in raster map randomly within a range specified
by user
- vary values of cells underneath vector objects ADDITIONALLY by
a range specified by user or attribute table fields.
- use result for fuzzy viewshed and cost models
*/
