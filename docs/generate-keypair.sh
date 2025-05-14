#!/bin/sh

if [ $# -ne 1 ]; then
  echo "Usage: $0 SITE_ID"
  exit -1
fi

p_site_id=$1
keypairs_dir=./keypairs
site_dir=$keypairs_dir/$p_site_id
secret_length=16

if [ -d "$site_dir" ]; then
  rm -rf $site_dir
fi

####################################################################
# Local
####################################################################
secret=$(LC_ALL=C tr -dc 'A-Za-z0-9!@#$%^&*()_+{}[]' < /dev/urandom | head -c $secret_length | xargs);
mkdir -p $site_dir
openssl genrsa -out $site_dir/private.pem 2048
openssl rsa -in $site_dir/private.pem -pubout -out $site_dir/pub.pem
openssl genrsa -aes128 -passout pass:$secret -out $site_dir/private_enc.pem
openssl rsa -in $site_dir/private_enc.pem -passin pass:$secret -out $site_dir/private_plain.pem

# final public key file
openssl rsa -in $site_dir/private_plain.pem -passin pass:$secret -out $site_dir/$p_site_id-public.pem -pubout

rm -rf $site_dir/pub.pem
rm -rf $site_dir/private_plain.pem
rm -rf $site_dir/private.pem

# final private key file - convert pkcs8 format
openssl pkcs8 -topk8 -passin pass:$secret -inform PEM -outform PEM -in $site_dir/private_enc.pem -out $site_dir/$p_site_id-private.pem -nocrypt

rm -rf $site_dir/private_enc.pem


echo "Secret: $secret" >> "$site_dir/SECRET.TXT"