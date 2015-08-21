# PlanIt

The Planit project is an event planner which has features built with contributional implementation.
There are two main features: fake event detection, and speaker suggestion.

##Fake event detection

The fake event detector uses a machine learning classifier to predict if an event is legitimate or not. It uses several internet soures and two algorithmic sources as features in a vecotr which is classified using the Naiv Bayes classifier implementation in the Weka machine learning library.

##Speaker suggestion

The speaker suggestion feature has 3 components which are themselves contributional implementations.

1) Keyword extraction - Multiple sources are used to extract or derive keywords for an event given its description.

2) Speaker searching - Several speaker bureau websites can be queried to scrape the details of public speakers.

3) Judging the best speaker to suggest - A small valued contributional implementation which uses a machine learning classifier to judge which speakers are the best to suggest. This CI relies on the gensim library https://radimrehurek.com/gensim/ (which is based on Word2Vec https://code.google.com/p/word2vec/). In order to use the functionality of gensim, there is a script which must be executed and can be found in /PlanIt/eventplanner/src/main/scripts/gensim/ with its documentation.


##Project structure

The Planit project is a maven project which depends on Michael Layzell's contributional implementation framework https://github.com/mystor/ci

For viewing data sets and training classifiers the Wek GUI interface is useful http://www.cs.waikato.ac.nz/ml/weka/
