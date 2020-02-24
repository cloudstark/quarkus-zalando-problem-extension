#!/bin/sh -l

function export_project_version() {
    CURRENT_PROJECT_VERSION=$(cat ./pom.xml | grep "<version>" | head -1 | awk -F'>' '{ print $2 }' | sed "s/[<\/version ]//g")
    echo "Project version: ${CURRENT_PROJECT_VERSION}"
    echo "::set-env name=PROJECT_VERSION::${CURRENT_PROJECT_VERSION}"
}

export_project_version
