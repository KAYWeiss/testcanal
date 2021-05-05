mkdir -p src/main/resources/files
wget -c https://datasets.imdbws.com/title.basics.tsv.gz -O - | gunzip >> src/main/resources/files/title.basics.tsv
wget -c https://datasets.imdbws.com/title.principals.tsv.gz -O - | gunzip >> src/main/resources/files/title.principals.tsv
wget -c https://datasets.imdbws.com/title.episode.tsv.gz -O - | gunzip >> src/main/resources/files/title.episode.tsv
wget -c https://datasets.imdbws.com/name.basics.tsv.gz -O - | gunzip >> src/main/resources/files/name.basics.tsv
