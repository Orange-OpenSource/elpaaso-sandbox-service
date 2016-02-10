#!/bin/bash
#
# Copyright (C) 2015 Orange
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

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
