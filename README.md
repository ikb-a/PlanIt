# PlanIt

The Planit project is an event planner which has features built with contributional implementation.
There are two main features: fake event detection, and speaker suggestion.

##Fake event detection

The fake event detector uses a machine learning classifier to predict if an event is legitimate or not. It uses several internet soures and two algorithmic sources as features in a vector which is classified using the Naive Bayes classifier implementation in the Weka machine learning library.

##Speaker suggestion

The speaker suggestion feature has 3 components which are themselves contributional implementations.

1) Keyword extraction - Multiple sources are used to extract or derive keywords for an event given its description.

2) Speaker searching - Several speaker bureau websites can be queried to scrape the details of public speakers.

3) Judging the best speaker to suggest - A small valued contributional implementation which uses a machine learning classifier to judge which speakers are the best to suggest. This CI relies on the gensim library https://radimrehurek.com/gensim/ (which is based on Word2Vec https://code.google.com/p/word2vec/). In order to use the functionality of gensim, there is a script which must be executed and can be found in /PlanIt/eventplanner/src/main/scripts/gensim/ with its documentation.


##Project structure

The Planit project is a maven project, and it depends on a branch of Michael Layzell's contributional implementation framework https://github.com/wginsberg/ci/tree/summer2015 as well as his existometer project https://github.com/mystor/existometer

For viewing data sets and training classifiers the Weka GUI interface is useful http://www.cs.waikato.ac.nz/ml/weka/

Each feature in the project has some sources in their own package, and some resources which are used by the sources, in their own package. In the sibling packages there are the additional needed utilites to contruct a CI. There is inconsistency between features with the structure of building features with CIs and using resources, so the best place to start is by looking at the sources and resources themselves to see what kind of CI can be built, and go from there. The actual compositions of sources, resources, utilities, are not polished state, but they can give an in idea of what CIs are possible to make from the sources.

##Required API Keys:
- Google Geocoding API Key (https://developers.google.com/maps/documentation/geocoding/start) (2500 free req/day)
- Yelp API Key (https://www.yelp.ca/developers/) (25000 free req/day)
- Bing Search API - Web Results Only Key (https://datamarket.azure.com/dataset/bing/searchweb) (5000 free req/month)
- Google CSE API Key (https://developers.google.com/custom-search/)
- Google Places API Key (https://developers.google.com/places/web-service/)
