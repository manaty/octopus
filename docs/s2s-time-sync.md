Create 2 configurations in IDE (with working directory set to project root directory):

Main class: net.manaty.octopusync.Main
Program arguments: --server --config=server/config/server-dev.yml

Main class: net.manaty.octopusync.Main
Program arguments: --server --config=server/config/server-dev2.yml

Run both, try to pause/stop one of the servers, resume, etc. Look at the logs.