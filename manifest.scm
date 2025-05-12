(define-module (manifest)
  #:use-module (gnu packages)
  #:use-module (guix packages)
  #:use-module (guix profiles)
  #:use-module (gnu packages base)
  #:use-module (gnu packages bash)
  #:use-module (gnu packages python)
  #:use-module (gnu packages java)
  #:use-module (gnu packages maven)
  #:use-module (gnu packages maths)
  #:export (%murelbench-manifest))

(define %murelbench-manifest
  (packages->manifest
   (list glibc
         `(,openjdk11 "jdk")
         python-wrapper
         maven
         bash
         gnuplot)))

%murelbench-manifest
