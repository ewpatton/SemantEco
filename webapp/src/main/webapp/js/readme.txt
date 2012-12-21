SemantEcoUI
----"pop-infowindow" lisenter
--------if water/air data
------------leftcolumngenerater
------------rightcolumngenerater
------------chartgenerater
------------first link click listener
             (use leftcolumngenerater,chartgenerater)
----------------queryForSiteMeasurementsCallback
------------second link click listener
                (use leftcolumngenerater,rightcolumngenerater,chartgenerater)
----------------queryForSiteMeasurementsCallback
------------------queryForNearbySpeciesCountsCallback
--------------------queryIfSiblingsExistCallback
                     (recursive queryForNearbySpeciesCountsCallback)
--------if bird data
------------leftcoloumgenerater
------------chartgenerator
------------first link click listener
----------------queryForSpeciesForASiteCallback

--------*if another domain*
         (if the data is same as water/air data or bird data, nothing need to be changed, just add the judgement marker.data.isSomething to corresponding if condition)
         (if the data is different, then another case is needed)
------------columngenerater
------------chartgenerater
------------***


----lightbox
--------init
--------clean
--------show


----chartTests
    (a function can be triggered in console for testing charts by use fake data)
