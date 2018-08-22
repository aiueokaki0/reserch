find . -iname '*.jar' -print | while read jar; do
  echo "$jar:"
  unzip -qq -l $jar | sed 's/.* //' | while read cls; do
    unzip -c $jar $cls | grep -q 'AKRescue' && echo "   "$cls
  done
done