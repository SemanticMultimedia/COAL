# import the library
from SPARQLWrapper import SPARQLWrapper, JSON

# (1) wrap the dbpedia SPARQL end-point
endpoint = SPARQLWrapper("http://dbpedia.org/sparql")

# (2) set the query string
endpoint.setQuery("""
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX dbpr: <http://dbpedia.org/resource/>
SELECT ?label
WHERE { dbpr:Dave_Brubeck rdfs:label ?label }
""")

# (3) select the return format (e.g. XML, JSON etc...)
endpoint.setReturnFormat(JSON)

# (4) execute the query and convert into Python objects
results = endpoint.query().convert()

# (5) interpret the results: 
for res in results["results"]["bindings"] :
	print res['label']['value']