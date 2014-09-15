# boot.task.tomcat
a local development server for boot 2.

## usage

from build.boot:
```bash
(deftask develop
  "Run service during development."
  []
  (comp (watch) (speak) (war) (serve)) )
```

from the command line:
```bash
boot serve -f /my/project-one.war -f /my/project-two.war wait
```
