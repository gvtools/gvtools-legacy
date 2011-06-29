This module needs a lot of work.

First and foremost, a complete, non-interactive GRASS interface
needs to be added.

Then, there are some efficiency optimizations to be applied.

Lastly, some of the assumptions need to be checked.
In particular, the "blind, good-faith" use of v.surf.bspline needs
to be rethought:
  - bicubic interpolation instead of bilinear + automatic estimation of lambda_i?
       -> use v.random.sample (part of former DST tools)
  - how to choose sie/sin?
  - is this really the best interpolator for LiDAR data?


These are my suggestions, to the module's author, Rebecca Bennett:
  
 In order to have it run under gvSIG, it needs to have a  complete
 user interface added (GRASS options and flags). Currently,  it
 takes interactive input and also has some hard-wired settings,
 all of  which will need to be removed and replaced by the corresponding
 GRASS  flags/options and temporary maps that can be deleted once
 the module has  finished its operation.
 
 The "echo" messages should be converted to  "g.message", so GRASS
 can display them properly.
 
  Regarding the choice  of neighborhood filter for the low pass filtering,
  I think you should replace  the .ASC files with a call to "r.neighbors".
  That does the same job, but can  simply take the radius of the filter window
  as input.
 
 Since LiDAR  processing involves such huge volumes of data, the
 script should be tweaked  for performance in all possible
 places. Thus:
 
 Lines 50-52:
 Better  use "r.to.vect" and directly create 3D points (no attribute
 table) from the  output of "v.to.points". Those can be processed very
 efficiently by  "v.surf.bspline".
 
 Line 56:
 Re. the call to "v.surf.bspline": change  this to directly process
 Z-coordinates instead of elevation attributes (see  above).
 I also see that you have hard-coded the parameter values  for
 "lambda_i", "sie" and "sin". According to the man page, 
 "sie" and  "sin" are resolution dependent:
 
 "As a general rule, spline step length  should be greater than 
 the mean distance between observation points (twice  the distance 
 between points is a good starting point)"
 
 I don't think  that this setting affects the quality of the
 result, but too short steps can  significantly increase
 processing time. Maybe this should be turned into an  option
 that the user can change to tweak perfomance (or think of
 a way to  come up with a good guess for an automatic setting).
 
 Some questions  regarding the processing:
 - Would it make sense to use a circular filter  window instead of
 a rectangular one? This may be more accurate, but  slower.
 - Your current selection of filter sizes is 5,7,9,11 and 39.
 Is  there a particular reason why recommend these and not, say,
 anything in  between 11 and 39 (or 3)? If so, that would have to
 go into the usage  notes.
 - How did you arrive at "dmax=10" for "v.to.points"?
 - Have you  made some experiments with the "lambda_i" parameter
 in "v.surf.bspline" to  check how smoothing affects your results?
 
 Please send me the details of  the dataset you have used for
 developing this module (extent, resolution, min  and max values),
 so I can make sure that your script does not contain  any
 code that is specific to the input data you used.

---

Here's one useful reply:

Just a quick question regarding the use of r.to.vect. How do I specify only the 
points that lie on the zero contour (i.e. the output of r.contour / 
v.to.points)? I don't want to create a vector of all points in the raster just 
those where the values of the lowpass and ground rasters are the same. Is there 
a way to limit by raster value?

SUGGESTED SOLUTION:

You can make a copy of the r.to.vect output map that preserves
only "0.0" value cells, using r.mapcalc's if() expression:

  result="if(map=="0.0",map,null())"

run r.to.vect on "result" and it should only pick up the cells
that represent your zero contours.


 
