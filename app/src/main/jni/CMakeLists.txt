cmake_minimum_required(VERSION 3.4.1)

add_library(audio-processor
            SHARED
        smbPitchShift.cpp)

target_link_libraries(audio-processor
           android
           log)