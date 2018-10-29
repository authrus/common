


"routes": [
	{
		"pattern": "/path",
		"template": "/path2"
		"server-group": "blah"
	},
	{
		"pattern": "/path",
		"template": "/path2"
		"server-group": "blah"
	},
	{
		"pattern": "/path",
		"template": "/path2"
		"server-group": "blah"
	}
],
"server-group": {
	"name": "blah",
	"buffer": 8192,
	"servers": [
		"http://localhost:8080/",
		"http://localhost:9090/"
	]
}