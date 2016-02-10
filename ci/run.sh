#!/bin/bash

#set -x

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
export ATC_URL=${ATC_URL:-"http://84.39.33.56:8080"}
export fly_target=${fly_target:-tutorial}
export pipeline=${pipeline:-elpaaso-sandbox-service}
echo "Concourse API target ${fly_target}"
echo "Concourse Pipeline ${pipeline}"
echo "Tutorial $(basename $DIR)"

pushd $DIR
  yes y | fly -t ${fly_target} set-pipeline -c pipeline.yml -p ${pipeline}
  curl $ATC_URL/pipelines/${pipeline}/jobs/jobs-elpaaso-sandbox-service/builds -X POST
  fly -t ${fly_target} watch -j ${pipeline}/jobs-elpaaso-sandbox-service
popd

#set +x
