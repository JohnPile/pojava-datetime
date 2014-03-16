mvn clean repository:bundle-create install
cd target
jar -uf datetime-3.0.1-bundle.jar *-j* *-s* *-3.?.?.*
