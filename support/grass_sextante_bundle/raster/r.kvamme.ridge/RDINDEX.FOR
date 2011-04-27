C    LAST UPDATE: 4/21/92
C
      REAL*4 FUNCTION rdindex ( e4,e3,e2,e5,e0,e1,e6,e7,e8 )
C ************************************************************
C     Main 3x3 window function for ridge-drainage index
C     Matrix arranged as follows:
C     e4, e3, e2
C     e5, e0, e1
C     e6, e7, e8
C ************************************************************
      REAL*4 e0,e1,e2,e3,e4,e5,e6,e7,e8,angle
      REAL*4 e(9)
      INTEGER*2 i,j,one
      LOGICAL*2 less(9)
      PARAMETER ( one=1 )
C
      e(1) = e1
      e(2) = e2
      e(3) = e3
      e(4) = e4
      e(5) = e5
      e(6) = e6
      e(7) = e7
      e(8) = e8
      e(9) = e1
      rdindex = 0.0
      DO i = one, 9
         less(i) = e(i) .LE. e0
      ENDDO
      DO i = one, 8
         j = i + one
         IF ( less(i) .and. less(j) ) THEN
            rdindex = rdindex + 45.0
         ELSE
            IF ( less(i) .OR. less(j) ) THEN
               rdindex = rdindex + angle( e0,e(i),e(j),i )
            ENDIF
         ENDIF
      ENDDO
      END
C
      REAL*4 FUNCTION angle ( e0,e1,e2,i )
C *** Function used by RDINDEX to compute angles **************
      REAL*4 e0,e1,e2,radian,base
      INTEGER*2 i
      PARAMETER ( radian = 57.29578 )
C
      IF ( MOD(i,2) .EQ. 0 ) THEN
         base = e2
      ELSE
         base = e1
      ENDIF
      angle = ATAN( (e0-base) / ABS( e1-e2 ) ) * radian
      IF ( angle .LT. 0.0 ) angle = angle + 45.0
      END
