/*
 *    Copyright 2020 SMB GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package solutions.cloudstark.quarkus.zalando.problem.runtime;

/**
 * Media Type for problem json.
 */
public class MediaType {

  public static final String APPLICATION_PROBLEM_JSON = "application/problem+json";

  public static final javax.ws.rs.core.MediaType APPLICATION_PROBLEM_JSON_TYPE =
      javax.ws.rs.core.MediaType.valueOf(APPLICATION_PROBLEM_JSON);
}
