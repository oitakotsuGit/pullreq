#!/usr/bin/perl

use utf8;
use Encode;

$target_file=$ARGV[0];
$date=`date +'%Y%m%d-%H%M'`;
chop($date);

$dict_file= "trans.dict";
$orig_file=$ARGV[0] . ".orig-$date";
$stringDate = localtime;
$stringDate =~ s/\s/_/g;


if ( ! -e $orig_file)  {
    $cmd="mv $target_file $orig_file";
    system($cmd);
}

open(FH, "$dict_file") || die "cannot open $dict_file\n";

while (<FH>)  {
    chop;
    next if /^$/;
    next if /^#/;
    $_ = Encode::decode_utf8($_);
    @tmp=split(/:::/);
    print "$tmp[0] -> $tmp[1]\n";
    $dict{$tmp[0]} = $tmp[1];
}

close(FH);

open(FHI, "$orig_file")  || die;
open(FHO, ">$target_file")  || die;

while (<FHI>)  {
    $_ = Encode::decode_utf8($_);
    foreach $k (keys(%dict))  {
#	if (/default view/ && $k=~/default view/)  {
#	    print "k: $k, dict: $dict{$k}\n";
#	    print;
#	}
	s/$k/$dict{$k}/g;
    }
    s///g;
    print FHO;
}

close(FHI);
close(FHO);

