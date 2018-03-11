# Redemption
[![Website](https://img.shields.io/website/https/redemption.bonevm.com.svg?label=website "Website")](https://redemption.bonevm.com)
[![Codeship Status for scionaltera/redemption](https://img.shields.io/codeship/d9773320-5bde-0135-1e8a-762f064b5c19/master.svg)](https://codeship.com/projects/237681)  
## Introduction
Redemption is a platform for the folks at [1UpOnCancer](https://1uponcancer.org) to use to manage the competitions and giveaways they do. They have to manage a lot of Steam keys and other prizes, participants and events and needed a database to help with that. I have decided to make it an open source project in case there are other organizations that need a similar way to organize their contest assets.

## Running
The easiest way to get Redemption running is to use the [Docker container](https://hub.docker.com/r/scionaltera/redemption/). You will need to supply some parameters to tell it where to find its database. They are listed in `secrets.env.example` and can be supplied as environment variables.

There is also a `docker-compose.yml` in the root of the project that you can use to easily start it up for local testing and development. If you'd like to use that, simply copy `secrets.env.example` to `secrets.env` and run `docker-compose up`.

## Support
The best way to get support for Redemption is to file a GitHub issue. We do not have a large development team and this work is being done on a volunteer basis, so support is on a best-effort basis only.

There currently is no documentation, but we are going to start building out a wiki in the near future. Contributions are welcome.
