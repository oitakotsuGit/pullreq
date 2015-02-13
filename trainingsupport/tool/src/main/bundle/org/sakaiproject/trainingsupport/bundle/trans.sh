#!/bin/csh -f

set files = "messages_ja.properties.UTF-8"

foreach f ($files)
  echo $f
  perl ./trans.perl $f
end
